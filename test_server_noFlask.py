from http.server import BaseHTTPRequestHandler, HTTPServer

class TestHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == '/test':
            self.send_response(200)
            self.send_header('Content-type', 'text/plain')
            self.end_headers()
            self.wfile.write(b'Verbindung erfolgreich!')
        else:
            self.send_response(404)
            self.end_headers()

def run(server_class=HTTPServer, handler_class=TestHandler):
    server_address = ('', 8080)
    httpd = server_class(server_address, handler_class)
    print('Server läuft auf http://localhost:8080/test ...')
    httpd.serve_forever()

run()
