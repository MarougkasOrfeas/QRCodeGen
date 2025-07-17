# QR Code Generator

A Java-based application that generates QR codes and optionally uploads them to Google Drive.  
Supports **Console**, **CLI**, and **GUI** modes.

---

## Features

- Generate QR codes from text or URLs
- Save to disk with custom width/height
- Upload generated or custom PDF files to **Google Drive**
- Supports external `config.properties` for customization
- Modular codebase: easy to extend and maintain
- JavaFX-based GUI for non-technical users

---

## Application Modes

### Console
- Prompts for input via terminal
- Displays QR save path or errors
<img width="1098" height="184" alt="image" src="https://github.com/user-attachments/assets/560d2d9e-263c-4500-9d5b-1fd3bc79e80b" />



### CLI
- Accepts arguments like:
  ```bash
  java -jar qr-generator.jar "https://www.urldecoder.org/" -w 250 -h 250 -o "C:\QR"
  ```
### GUI (JavaFX)
- User-friendly graphical interface
- Enter text or URL
- Customize size and click Generate
- Upload PDF files to Google Drive
<img width="1910" height="1004" alt="image" src="https://github.com/user-attachments/assets/6d69cb50-72b3-4245-b280-bf7466de6946" />


## Upload Functionality
Uploads are supported only for Google Drive.
The app integrates with the Google Drive API using OAuth2 authentication.

## Google Drive API Setup
To enable upload functionality:

- Go to Google Cloud Console
- Create a new project (or use an existing one)
- Enable the Google Drive API
- Navigate to: APIs & Services → Credentials
- Click "Create Credentials" → "OAuth client ID"
- Choose "Desktop App" and download the client_secret.json
- Place the file and update config.properties:

```properties
google.drive.client.secret.path=C:/path/to/client_secret.json
google.drive.folder.id=your_folder_id
```
- Make sure your app is in Testing mode or Published
- Add your Google account under OAuth Consent Screen > Test Users

## Configuration
The application uses a config.properties file for settings like:
```
output.path=disired_path
filename.use.uuid=false/true
google.drive.client.secret.path=C:/path/to/client_secret.json
google.drive.folder.id=your_google_drive_folder_id
max.upload.file.size.bytes=max_number_in_bytes
accepted.file.types=pdf
```

## External Config Support (Override)
You can supply an external config file via:

✅ System Property
```bash
java -DQR_CONFIG_PATH=/path/to/config.properties -jar qr-generator.jar
```

## Environment Variable
```bash
export QR_CONFIG_PATH=/path/to/config.properties
```
If no external config is found, the app uses the one bundled inside the JAR.

## ☁️ Google Drive Integration
- Uses OAuth2 with drive.file scope
- Requires Google Drive API credentials from Google Cloud Console
- On first run, you'll be prompted to authenticate via browser

## Requirements
- Java 21
- Internet access (only for uploading to Drive)
- Google Drive API credentials (client_secret.json)
- Google account with access to the target Drive folder

## Build & Run
Build with Maven
```bash
mvn clean install
```
This will compile the project and create a runnable JAR in the target/ directory.

## Run the Application
🖥️ Run GUI (JavaFX)
bash
```
mvn javafx:run
```

💻 Run CLI or Console Mode (Example)
```bash
java -jar target/qr-generator.jar --text="https://example.com" --width=300 --height=300
```


