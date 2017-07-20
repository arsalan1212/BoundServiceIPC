# BoundServiceIPC
In order to communicate between two different processes for that we use 
1) Messanger
2)AIDL(Android interface Defination Language)

But the recommended approach from Google is to use Messanger because the Messanger use single Thread that means i accept one request at a time
and also process one request at a time, in case of messanger the thread is safe
While in case of AIDL that use Multithread that means it handle more request at a time and also process it but the thread is not safe by default
you have to make it safe manually
