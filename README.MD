# **Java Concurrent Database Access (JCDA)** #

Executes database queries in a separate Thread and returns result via callback object

Usage:
Create database object and initialize it.
Add annotation "@Query" to set the query in your interface.
Use "@Bind" to bind place holders in the query. Example: variable "id" will replace the place-holder "{{id}}" in the query.
Implement BroadcastReceiver in the class where you want to receive the results.

**Reflection is used to define the interface callbacks.