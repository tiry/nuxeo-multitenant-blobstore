
## About 

This module provides a `BlobDispatcher` that :

 - dynamically registers `AESBinaryManager` for each tennant 
 - dispatch BlobStorage between them according to target tenant

## Building

    mvn clean install

## Deploying

Install [the Nuxeo Multi Tenant Marketplace Package](https://connect.nuxeo.com/nuxeo/site/marketplace/package/nuxeo-multi-tenant).

Build this module and manually copy the built artifacts into `$NUXEO_HOME/templates/custom/bundles/` and activate the "custom" template.

# About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Netflix, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at www.nuxeo.com.
