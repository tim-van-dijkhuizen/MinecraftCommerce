# Set up SSL using CloudFlare
CloudFlare is well known for their DDOS-protection, but they also offer free SSL certificates. Follow the steps below to set up SSL using CloudFlare.

### Steps

1. [Add your domain to CloudFlare](https://support.cloudflare.com/hc/en-us/articles/201720164-Creating-a-Cloudflare-account-and-adding-a-website)
2. [Create an origin certificate](https://support.cloudflare.com/hc/en-us/articles/115000479507-Managing-Cloudflare-Origin-CA-certificates#h_30e5cf09-6e98-48e1-a9f1-427486829feb)
3. Create two .pem files inside the MinecraftCommerce plugin folder and place the Origin Certificate and Private Key content inside of them. Then open the configuration menu and set the SSL certificate and SSL Private Key options.
4. [Set SSL/TLS encryption mode to Full (strict)](https://support.cloudflare.com/hc/en-us/articles/115000479507-Managing-Cloudflare-Origin-CA-certificates#h_117fcdd6-a0bb-4b20-b4b7-338e101747a6)

You should now be able to access the MinecraftCommerce webserver using SSL.