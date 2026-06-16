param(
    [Parameter(Mandatory=$true)]
    [string]$RootPassword,
    [string]$DbName = "task_manager_db",
    [string]$DbUser = "task_user",
    [string]$DbPass = "task_password",
    [string]$Host = "localhost"
)

function Write-Err { Write-Host "ERROR: $args" -ForegroundColor Red }

if (-not (Get-Command mysql -ErrorAction SilentlyContinue)) {
    Write-Err "`nMySQL client 'mysql' not found in PATH. Install MySQL or add the MySQL client to PATH.`n"
    Write-Host "Options:" -ForegroundColor Yellow
    Write-Host " - Install MySQL and ensure 'mysql.exe' is on PATH" -ForegroundColor Yellow
    Write-Host " - Or run the project's Docker Compose which provides a database service" -ForegroundColor Yellow
    exit 1
}

$createDbSql = @"
CREATE DATABASE IF NOT EXISTS `$DbName`;
CREATE USER IF NOT EXISTS '$DbUser'@'$Host' IDENTIFIED BY '$DbPass';
GRANT ALL PRIVILEGES ON `$DbName`.* TO '$DbUser'@'$Host';
FLUSH PRIVILEGES;
"@

Write-Host "Creating database '$DbName' and user '$DbUser' on host '$Host'..."

try {
    & mysql -u root -p$RootPassword -h $Host -e $createDbSql
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Database and user created successfully." -ForegroundColor Green
        Write-Host "Make sure to update src/main/resources/application.properties with the DB credentials." -ForegroundColor Cyan
    } else {
        Write-Err "mysql exited with code $LASTEXITCODE"
        exit $LASTEXITCODE
    }
} catch {
    Write-Err "Failed to execute mysql: $_"
    exit 1
}
