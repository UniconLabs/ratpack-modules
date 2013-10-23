package net.unicon.iamlabs.ratpack.cas

import com.google.inject.Binder
import com.google.inject.Injector
import groovy.xml.XmlUtil
import org.ratpackframework.guice.HandlerDecoratingModule
import org.ratpackframework.handling.Context
import org.ratpackframework.handling.Handler
import org.ratpackframework.session.store.SessionStorage

class CasHandler implements Handler {
    String hostPort
    String casUrl
    Handler rest

    @Override
    void handle(Context context) {
        if (!context.get(SessionStorage).remoteUser) {
            if (context.request.queryParams.ticket) {
                println "validating ticket"
                def url = "${this.casUrl}/serviceValidate?service=${URLEncoder.encode("${hostPort}${context.request.uri}".replaceAll(/\??&?ticket=.*$/, ''), 'UTF-8')}&ticket=${context.request.queryParams.ticket}"
                def casResponse = new XmlSlurper().parse(url).declareNamespace(cas: 'http://www.yale.edu/tp/cas')
                if (casResponse.'cas:authenticationFailure'.size()) {
                    println "There was a failure authenticating: casResponse: ${XmlUtil.serialize(casResponse)}"
                    context.response.send('There was an error. Contact support')
                    return
                }
                if (casResponse.'cas:authenticationSuccess'.size()) {
                    println "success"
                    def username = casResponse.'cas:authenticationSuccess'.text()
                    context.get(SessionStorage).remoteUser = username
                }
            } else {
                context.redirect("${casUrl}/login?service=${URLEncoder.encode("${hostPort}${context.request.uri}", 'UTF-8')}")
                return
            }
        }
        rest.handle(context)
    }
}

class CasModule implements HandlerDecoratingModule {
    String hostPort
    String casUrl

    @Override
    Handler decorate(Injector injector, Handler handler) {
        return new CasHandler(rest: handler, hostPort: this.hostPort, casUrl: this.casUrl)
    }

    @Override
    void configure(Binder binder) {}
}