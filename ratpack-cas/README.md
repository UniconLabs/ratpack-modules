# Ratpack CAS

## Quick Start

1. install lazybones. it's great. use [gvm](http://gvmtool.net/).

    ```
    gvm install lazybones
    ```
1. create project.

    ```
    lazybones create ratpack ${projectName}
    ```

1. modify `build.gradle`. Add the following.

    ```groovy
    ...
    repositories {
        ...
        maven { url "http://dl.bintray.com/uniconlabs/maven" }
        ...
    }
    ...
    dependencies {
        ...
        compile "net.unicon.iamlabs.ratpack:ratpack-cas:0.0.3"
        ...
    }
    ...
    ```

1. add to `src/ratpack/ratpack.groovy`.

    ```groovy
    ...
    import org.ratpackframework.session.store.MapSessionsModule
    import org.ratpackframework.session.store.SessionStorage
    import net.unicon.iamlabs.ratpack.cas.CasModule
    ...
    ratpack {
        ...
        modules {
            ...
            register(new MapSessionsModule(10, 5))
            register(new CasModule(hostPort: 'http://localhost:5050', casUrl: 'https://test.scaldingspoon.org/cas'))
            ...
        }
        ...
    }
    ...
    ```

1. add a handler to take advantage of it in `src/ratpack/ratpack.groovy`

    ```groovy
    ...
    ratpack {
        ...
        handlers {
            ...
            get("hello") {
                response.send("hello, ${get(SessionStorage).remoteUser}")
            }
            ...
        }
        ....
    }
    ...
    ```

1. start server

    ```
    ./gradlew run
    ```

1. check it out [http://localhost:5050/hello](http://localhost:5050/hello)