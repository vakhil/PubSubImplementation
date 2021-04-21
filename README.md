# PubSub Deep Dive

This project is a multi module with client and server. Firstly, go to the server module and start the server. Then, go the client and start multiple client instances.
You can see the server terminal indicating the number of client instances connecting to it !!!
With each client instance , we spawn a new thread on the server side. Each client instance will have 2 threads running, one for listening to the Server's messages and one
for listening for the keyboard input.

On the client instance, if you want to subscribe to the topic 'NEWS' , type this `SUBSCRIBE NEWS`
Get both the client instances to subscribe to the topic 'NEWS' and then type `PUBLISH NEWS "Covid News"`. This will send the message on the topic and the other clients would receive the message




