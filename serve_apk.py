#!/usr/bin/env python3
import http.server
import socketserver
import qrcode
import socket
import os
import subprocess
import re
import sys

APK_PATH = os.path.join('app', 'build', 'outputs', 'apk', 'debug', 'app-debug.apk')
PORT = 8000

if not os.path.exists(APK_PATH):
    print('APK not found. Build it with ./gradlew assembleDebug first.')
    exit(1)

def qr_print(text: str) -> None:
    qr = qrcode.QRCode(border=2)
    qr.add_data(text)
    qr.make(fit=True)
    qr.print_ascii(invert=True)

if "--upload" in sys.argv:
    print("Uploading APK to bashupload.com ...")
    curl_cmd = [
        "curl",
        "--silent",
        "--upload-file",
        APK_PATH,
        f"https://bashupload.com/{os.path.basename(APK_PATH)}",
    ]
    result = subprocess.run(curl_cmd, capture_output=True, text=True)
    match = re.search(r"https://bashupload.com/\S+", result.stdout)
    if not match:
        print("Failed to upload APK")
        sys.exit(1)
    url = match.group(0)
    print(f"APK uploaded to {url}")
    qr_print(url)
    print("Scan the QR code or open the URL above to download")
else:
    def get_ip():
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        try:
            s.connect(('8.8.8.8', 80))
            return s.getsockname()[0]
        finally:
            s.close()

    ip = get_ip()
    url = f'http://{ip}:{PORT}/app-debug.apk'
    qr_print(url)
    os.chdir(os.path.dirname(APK_PATH))
    Handler = http.server.SimpleHTTPRequestHandler
    with socketserver.TCPServer(('', PORT), Handler) as httpd:
        print(f'Serving {APK_PATH} at {url}')
        print('Press Ctrl+C to stop.')
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            pass
