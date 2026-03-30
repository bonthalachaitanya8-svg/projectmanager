# Run this script once in PowerShell to install Maven and start the app
# Right-click PowerShell -> Run as Administrator, then:
# Set-ExecutionPolicy RemoteSigned
# .\run-windows.ps1

$mavenVersion = "3.9.6"
$mavenDir = "$env:USERPROFILE\.m2\wrapper\apache-maven-$mavenVersion"
$mavenBin = "$mavenDir\bin\mvn.cmd"
$projectDir = $PSScriptRoot

# Check if mvn is already on PATH
if (Get-Command mvn -ErrorAction SilentlyContinue) {
    Write-Host "Maven found on PATH. Starting app..." -ForegroundColor Green
    Set-Location $projectDir
    mvn spring-boot:run
    exit
}

# Download Maven if not present
if (-not (Test-Path $mavenBin)) {
    Write-Host "Downloading Apache Maven $mavenVersion..." -ForegroundColor Yellow
    $zipUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$mavenVersion/apache-maven-$mavenVersion-bin.zip"
    $zipPath = "$env:TEMP\maven.zip"
    New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\.m2\wrapper" | Out-Null
    Invoke-WebRequest -Uri $zipUrl -OutFile $zipPath
    Expand-Archive -Path $zipPath -DestinationPath "$env:USERPROFILE\.m2\wrapper" -Force
    Remove-Item $zipPath
    Write-Host "Maven downloaded successfully." -ForegroundColor Green
}

# Run the app
Write-Host "Starting Spring Boot application..." -ForegroundColor Green
Write-Host "Open http://localhost:8080/login in your browser" -ForegroundColor Cyan
Set-Location $projectDir
& "$mavenBin" spring-boot:run
