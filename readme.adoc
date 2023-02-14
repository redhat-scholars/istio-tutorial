= Polyglot microservices (Java, Node, .NET) + Istio on Kubernetes/OpenShift
This content is brought to you by http://developers.redhat.com - Register today!
:toc: macro
:toc-title: Table of Contents
:toclevels: 3
:icons: font
:data-uri:
:source-highlighter: highlightjs


- A HTML version of this file is published at https://redhat-scholars.github.io/istio-tutorial/

- The source code is available at https://github.com/redhat-scholars/istio-tutorial

- Download the ebook "Introducing Istio Service Mesh for Microservices" for FREE at https://developers.redhat.com/books/introducing-istio-service-mesh-microservices/.

- If you are in a hurry and want to get hands-on with Istio insanely fast, just go to https://developers.redhat.com/topics/service-mesh and start instantly.



There are three different and super simple microservices in this system and they are chained together in the following sequence:

```
customer → preference → recommendation
```

== Local Development

Tested with Node.js v18.9.

```bash
# Clone the repository locally
git clone git@github.com:redhat-scholars/istio-tutorial.git
cd istio-tutorial

# Install antora dependencies
npm install

# Performs an initial build, then watches adoc files
# and rebuilds the site when they are modified
npm run dev
```

Open the _gh-pages/index.html_ to see the site. You can refresh to see your
changes if you modify _adoc_ files, since the `npm run dev` script will
automatically rebuild the site when it detects changes.