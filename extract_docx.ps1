Add-Type -AssemblyName System.IO.Compression.FileSystem

$docxPath = "C:\Users\moham\Desktop\focusflow\FocusFlow_Complete_App_Specification_and_Agent_Prompt.docx"
$outputPath = "C:\Users\moham\Desktop\focusflow\spec.txt"

$zip = [System.IO.Compression.ZipFile]::OpenRead($docxPath)
$entry = $zip.Entries | Where-Object { $_.FullName -eq "word/document.xml" }
$stream = $entry.Open()
$reader = New-Object System.IO.StreamReader -ArgumentList $stream
$content = $reader.ReadToEnd()
$reader.Close()
$stream.Close()
$zip.Dispose()

$xmlDoc = New-Object System.Xml.XmlDocument
$xmlDoc.LoadXml($content)

$nsMgr = New-Object System.Xml.XmlNamespaceManager -ArgumentList $xmlDoc.NameTable
$nsMgr.AddNamespace("w", "http://schemas.openxmlformats.org/wordprocessingml/2006/main")

$paragraphs = $xmlDoc.SelectNodes("//w:p", $nsMgr)
$lines = @()
foreach ($p in $paragraphs) {
    $runs = $p.SelectNodes(".//w:t", $nsMgr)
    $lineText = ""
    foreach ($r in $runs) {
        $lineText += $r.InnerText
    }
    $lines += $lineText
}

$lines -join "`n" | Out-File -FilePath $outputPath -Encoding utf8
Write-Host "Extracted $($lines.Count) paragraphs"
