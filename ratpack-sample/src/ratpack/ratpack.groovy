import net.unicon.iamlabs.ratpack.cas.CasHandler
import org.ratpackframework.session.store.MapSessionsModule
import org.ratpackframework.session.store.SessionStorage

import static org.ratpackframework.groovy.RatpackScript.ratpack
import static org.ratpackframework.groovy.Template.groovyTemplate

def casHandler = new CasHandler(hostPort: "http://localhost:5050", casUrl: "https://test.scaldingspoon.org/cas")

ratpack {
    modules {
        register(new MapSessionsModule(10, 5))
    }
    handlers {
        get "hello", casHandler
        get "hello", {
            response.send "hello, ${get(SessionStorage).remoteUser}"
        }
        
        assets "public"
    }
}
