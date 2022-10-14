import socket
 
HOST = "localhost"
PORT = 5001
 
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect((HOST, PORT))
 
sock.send("Hello000\n".encode())
data = sock.recv(1024)
print(data)

while True:
    msg = input().encode()
    # print(msg.encode())
    sock.send(msg)
    data = sock.recv(1024)
    print(data)

