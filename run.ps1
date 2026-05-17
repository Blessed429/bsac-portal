$ErrorActionPreference = "Stop"

if (!(Test-Path "jdk")) {
    Write-Host "Downloading OpenJDK 17 (Required to compile the app)..."
    Invoke-WebRequest -Uri "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.11%2B9/OpenJDK17U-jdk_x64_windows_hotspot_17.0.11_9.zip" -OutFile "jdk.zip"
    Write-Host "Extracting JDK..."
    Expand-Archive -Path "jdk.zip" -DestinationPath "jdk" -Force
}

# Set JAVA_HOME to the downloaded JDK so Maven uses it
$env:JAVA_HOME = "$PWD\jdk\jdk-17.0.11+9"

if (!(Test-Path "maven\apache-maven-3.9.6\bin\mvn.cmd")) {
    Write-Host "Downloading Maven..."
    Invoke-WebRequest -Uri "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip" -OutFile "maven.zip"
    Write-Host "Extracting Maven..."
    Expand-Archive -Path "maven.zip" -DestinationPath "maven" -Force
}

Write-Host "Compiling and Starting BSAC Application..."
.\maven\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run
