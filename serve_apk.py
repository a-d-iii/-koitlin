#!/usr/bin/env python3
import http.server
import socketserver
import qrcode
import socket
import os

APK_PATH = os.path.join('app', 'build', 'outputs', 'apk', 'debug', 'app-debug.apk')
PORT = 8000

if not os.path.exists(APK_PATH):
    print('APK not found. Build it with ./gradlew assembleDebug first.')
    exit(1)

def get_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        s.connect(('8.8.8.8', 80))
        return s.getsockname()[0]
    finally:
        s.close()

ip = get_ip()
url = f'http://{ip}:{PORT}/app-debug.apk'

qr = qrcode.QRCode(border=2)
qr.add_data(url)
qr.make(fit=True)
qr.print_ascii(invert=True)

os.chdir(os.path.dirname(APK_PATH))
Handler = http.server.SimpleHTTPRequestHandler
with socketserver.TCPServer(('', PORT), Handler) as httpd:
    print(f'Serving {APK_PATH} at {url}')
    print('Press Ctrl+C to stop.')
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass
