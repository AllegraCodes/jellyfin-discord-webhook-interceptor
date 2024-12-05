General Instructions:
1. Add your discord webhook url to application.properties
2. Set the template in the jellyfin webhooks plugin to jellyfinTemplate.handlebars
3. Point the jellyfin webhooks plugin to the machine running this program, plus the path /webhook
4. Recieve discord messages with the thumbnail

Docker Instructions:
1. Follow instructions above, using the container name in step 3
2. Provide your docker hub username and password in build.gradle.kts
3. Set the image name
4. See compose.yml for an example of use
