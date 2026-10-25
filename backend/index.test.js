const assert = require("node:assert/strict");
const { readFileSync } = require("node:fs");
const { test } = require("node:test");
const vm = require("node:vm");

function harness(fetchImpl) {
  const counts = new Map();
  const database = {
    collection: (collection) => ({ doc: (uid) => ({ key: `${collection}/${uid}` }) }),
    runTransaction: async (fn) => fn({
      get: async (ref) => ({ data: () => counts.get(ref.key) }),
      set: (ref, value) => counts.set(ref.key, value),
    }),
  };
  class HttpsError extends Error {
    constructor(code, message) { super(message); this.code = code; }
  }
  const modules = {
    "firebase-admin/app": { initializeApp() {} },
    "firebase-admin/firestore": { getFirestore: () => database },
    "firebase-functions/v2/https": { HttpsError, onCall: (_options, callback) => callback },
    "firebase-functions/params": { defineSecret: () => ({ value: () => "test-only-key" }) },
  };
  const context = { exports: {}, require: (name) => modules[name], fetch: fetchImpl, AbortSignal, Date };
  vm.runInNewContext(readFileSync(`${__dirname}/index.js`, "utf8"), context);
  return context.exports.generateAi;
}

const success = (content = "A useful response") => ({ ok: true, json: async () => ({ choices: [{ message: { content } }] }) });
const request = (data = { operation: "tip" }, uid = "owner") => ({ auth: { uid }, data });

test("unauthenticated and invalid requests never call the provider", async () => {
  let calls = 0;
  const generate = harness(async () => { calls++; return success(); });
  await assert.rejects(generate({ data: { operation: "tip" } }), { code: "unauthenticated" });
  await assert.rejects(generate(request({ operation: "chat", prompt: "" })), { code: "invalid-argument" });
  assert.equal(calls, 0);
});

test("network failure on primary model attempts fallback", async () => {
  const models = [];
  const generate = harness(async (_url, options) => {
    models.push(JSON.parse(options.body).model);
    if (models.length === 1) throw new Error("Network unavailable");
    return success();
  });
  const result = await generate(request());
  assert.equal(result.content, "A useful response");
  assert.deepEqual(models, ["openai/gpt-oss-20b", "groq/compound-mini"]);
});

test("provider failures produce an error instead of invented content", async () => {
  const generate = harness(async () => ({ ok: false }));
  await assert.rejects(generate(request({ operation: "breakdown", goal: "Learn Kotlin" })), { code: "unavailable" });
});

test("request limit is scoped to each user", async () => {
  const generate = harness(async () => success());
  for (let i = 0; i < 10; i++) await generate(request());
  await assert.rejects(generate(request()), { code: "resource-exhausted" });
  assert.equal((await generate(request({ operation: "tip" }, "another-owner"))).content, "A useful response");
});

test("chat includes bounded history and one copy of the current prompt", async () => {
  let payload;
  const generate = harness(async (_url, options) => { payload = JSON.parse(options.body); return success(); });
  const history = Array.from({ length: 12 }, (_, i) => ({ role: "user", content: `Past ${i}` }));
  await generate(request({ operation: "chat", prompt: "Current question", history }));
  assert.equal(payload.messages.length, 10);
  assert.equal(payload.messages.filter((m) => m.content === "Current question").length, 1);
  assert.equal(payload.messages[1].content, "Past 4");
});
