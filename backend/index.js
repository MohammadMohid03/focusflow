const { initializeApp } = require("firebase-admin/app");
const { getFirestore } = require("firebase-admin/firestore");
const { HttpsError, onCall } = require("firebase-functions/v2/https");
const { defineSecret } = require("firebase-functions/params");

initializeApp();

const db = getFirestore();
const groqApiKey = defineSecret("GROQ_API_KEY");
const primaryModel = "openai/gpt-oss-20b";
const fallbackModel = "groq/compound-mini";
const maxInputLength = 6000;

async function enforceRateLimit(uid) {
  const now = new Date();
  const minute = Math.floor(now.getTime() / 60000);
  const day = now.toISOString().slice(0, 10);
  const ref = db.collection("aiUsage").doc(uid);

  await db.runTransaction(async (transaction) => {
    const snapshot = await transaction.get(ref);
    const data = snapshot.data() || {};
    const minuteCount = data.minute === minute ? (data.minuteCount || 0) : 0;
    const dayCount = data.day === day ? (data.dayCount || 0) : 0;

    if (minuteCount >= 10 || dayCount >= 100) {
      throw new HttpsError("resource-exhausted", "AI request limit reached. Try again later.");
    }

    transaction.set(ref, {
      minute,
      minuteCount: minuteCount + 1,
      day,
      dayCount: dayCount + 1,
      updatedAt: new Date().toISOString(),
    });
  });
}

function buildMessages(data) {
  const operation = data.operation;
  if (operation === "chat") {
    const prompt = typeof data.prompt === "string" ? data.prompt.trim() : "";
    if (!prompt || prompt.length > maxInputLength) {
      throw new HttpsError("invalid-argument", "Prompt is required and must be within the size limit.");
    }
    const history = Array.isArray(data.history) ? data.history.slice(-8) : [];
    const messages = [{
      role: "system",
      content: "You are FocusFlow AI, a concise study and productivity assistant. Give accurate, practical answers.",
    }];
    for (const item of history) {
      if (!item || !["user", "assistant"].includes(item.role) || typeof item.content !== "string") continue;
      if (item.content.length <= maxInputLength) messages.push({ role: item.role, content: item.content });
    }
    messages.push({ role: "user", content: prompt });
    return { messages, temperature: 0.7, max_tokens: 1024 };
  }

  if (operation === "breakdown") {
    const goal = typeof data.goal === "string" ? data.goal.trim() : "";
    if (!goal || goal.length > maxInputLength) {
      throw new HttpsError("invalid-argument", "Goal is required and must be within the size limit.");
    }
    return {
      messages: [
        { role: "system", content: 'Break the goal into 4 to 6 useful subtasks. Return only JSON: {"tasks":[{"title":"...","description":"...","estimatedMinutes":25,"priority":"MEDIUM"}]}. Priority is URGENT, HIGH, MEDIUM, or LOW.' },
        { role: "user", content: `Goal: ${goal}` },
      ],
      temperature: 0.4,
      max_tokens: 1024,
      response_format: { type: "json_object" },
    };
  }

  if (operation === "schedule") {
    const tasks = Array.isArray(data.tasks)
      ? data.tasks.filter((task) => typeof task === "string").slice(0, 30).map((task) => task.slice(0, 300))
      : [];
    const hours = Number.isInteger(data.availableHours) ? Math.min(24, Math.max(1, data.availableHours)) : 1;
    return {
      messages: [
        { role: "system", content: `Create a practical study schedule for ${hours} available hours. Return only JSON: {"schedule":[{"title":"...","startTime":"09:00 AM","durationMinutes":45,"category":"STUDY"}],"summary":"..."}.` },
        { role: "user", content: `Tasks to schedule: ${tasks.join(", ") || "General study and deep work"}` },
      ],
      temperature: 0.5,
      max_tokens: 1024,
      response_format: { type: "json_object" },
    };
  }

  if (operation === "tip") {
    return {
      messages: [
        { role: "system", content: "Give one concise, practical focus tip in plain text." },
        { role: "user", content: "Give me an actionable focus tip." },
      ],
      temperature: 0.8,
      max_tokens: 120,
    };
  }

  throw new HttpsError("invalid-argument", "Unsupported AI operation.");
}

async function callGroq(payload, apiKey, model) {
  try {
    const response = await fetch("https://api.groq.com/openai/v1/chat/completions", {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${apiKey}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ model, ...payload }),
      signal: AbortSignal.timeout(12000),
    });
    if (!response.ok) return null;
    const body = await response.json();
    const content = body?.choices?.[0]?.message?.content;
    return typeof content === "string" && content.length <= 20000 ? content : null;
  } catch {
    return null; // Allow the fallback after network errors, invalid JSON, or timeouts.
  }
}

exports.generateAi = onCall({
  region: "us-central1",
  secrets: [groqApiKey],
  timeoutSeconds: 35,
  memory: "256MiB",
}, async (request) => {
  const uid = request.auth?.uid;
  if (!uid) throw new HttpsError("unauthenticated", "Sign in to use FocusFlow AI.");
  await enforceRateLimit(uid);

  const payload = buildMessages(request.data || {});
  const key = groqApiKey.value();
  if (!key) throw new HttpsError("unavailable", "AI service is not configured.");

  try {
    const content = await callGroq(payload, key, primaryModel)
      || await callGroq(payload, key, fallbackModel);
    if (!content) throw new HttpsError("unavailable", "AI service did not return a response.");
    return { content };
  } catch (error) {
    if (error instanceof HttpsError) throw error;
    throw new HttpsError("unavailable", "AI service is temporarily unavailable.");
  }
});
