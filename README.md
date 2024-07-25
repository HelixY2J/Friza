# Friza 

Friza is a ZAP add-on that integrates with Frida allowing us to manipulate application behaviour at realtime without the need of reversing the applciation with the help of API provided by Frida.

Frida is a dynamic instrumentation toolkit that allows us to inject custom scripts into running processes. Using Frida's RPC (Remote Procedure Call) mechanism,we can expose JavaScript functions to be called from our host application.

![Architecture](/img/arch.png)

We dont have to either reverse the application or reimplement the complex plugin again. If we have to decrypt a message, we can use friza to ask the application to decrypt the message for us by triggering specific functions exported by Frida 