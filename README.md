# The Athena SQS Project

This project is developed for user who want to send a large text message thru Amazon SQS. Amazon Simple Queue Service (SQS) is a cloud service for reliable messaging. The SQS service with its queues is located off-host. So, similar to the elastic load balancing service, or the relational database service, you can use the service without having to start an EC2 instance.
The problem of SQS is that one message body can be up to 64 KB of text in any format (default is 8KB). For larger messages you have to store the message somewhere else reliably, e.g. in S3, SimpleDB or RDS, and pass around a reference to the storage location instead of passing the message itself.
Also, encryption is not a built-in SQS feature, but depending on your privacy requirements you can consider encrypting the content of your message at an application level. Also, there is no built-in compression feature, but you can compress large messages at an application level before sending them.
If you use this SQS library, you can send a large message(over 64Kb).

## Features
- Large message support 
- Sending one message body can be up to unlimited by partitioning the message.
- Compress Compressing body message for large message.(GZIP format).

## Links
- Company Web Site: http://opensourceconsulting.co.kr, http://www.osci.kr
- Facebook: http://www.facebook.com/osckorea

## Developer Information
- Athena SQS is setup to build using [Maven](http://maven.apache.org/)
- You need JDK 6 or higher to __build__ this project. 

Enjoy this!