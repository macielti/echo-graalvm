(ns echo-graalvm.components
  (:require [common-clj.integrant-components.config :as component.config]
            [echo-graalvm.diplomat.telegram.consumer :as diplomat.telegram.consumer]
            [http-client-component.core :as component.http-client]
            [integrant.core :as ig]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.tools.logging]
            [telegrama.component :as component.telegram])
  (:gen-class))

(taoensso.timbre.tools.logging/use-timbre)

(def config
  {::component.config/config           {:path "resources/config.edn"
                                        :env  :prod}
   ::component.http-client/http-client {:components {:config (ig/ref ::component.config/config)}}
   ::component.telegram/consumer       {:settings   diplomat.telegram.consumer/settings
                                        :components {:config      (ig/ref ::component.config/config)
                                                     :http-client (ig/ref ::component.http-client/http-client)}}})

(defn start-system! []
  (timbre/set-min-level! :debug)
  (ig/init config))

(def -main start-system!)
