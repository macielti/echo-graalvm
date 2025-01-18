(ns echo-graalvm.telegram-consumer
  (:require [cheshire.core :as json]
            [clojure.tools.logging :as log]
            [http-client-component.core :as component.http-client]
            [service-component.error :as service.error]
            [integrant.core :as ig]
            [schema.core :as s]))

(s/defn diplomat-fetch-updates!
  [offset :- (s/maybe s/Int)
   limit :- (s/maybe s/Int)
   telegram-token :- s/Str
   http-client]
  (let [{:keys [body status] :as response} @(component.http-client/request! {:url         (str "https://api.telegram.org/bot" telegram-token "/getUpdates" "?offset=" offset "&limit=" limit "&timeout=1")
                                                                             :method      :get
                                                                             :endpoint-id :fetch-telegram-bot-updates} http-client)]
    (when (>= status 400)
      (log/error ::error-fetch-telegram-bot-updates response)
      (service.error/http-friendly-exception status
                                             "error-fetch-telegram-bot-updates"
                                             "Error while fetch Telegram Bot updates"
                                             {:error ::error-fetch-telegram-bot-updates}))
    (-> body
        (json/decode true)
        :result)))

(defn fetch-updates!
  [offset limit http-client config]
  (diplomat-fetch-updates! (or offset 0) (or limit 100) (:telegram-token config) http-client))

(defmethod ig/init-key ::telegram-consumer
  [_ {{:keys [http-client config]} :components
      main-handler-fn!             :main-handler-fn}]
  (let [current-offset (atom nil)]
    (log/info :starting ::telegram-consumer)
    (while true
      (try (doseq [update (fetch-updates! @current-offset 100 http-client config)]
             (main-handler-fn! update)
             (reset! current-offset (-> update :update_id inc)))
           (catch Exception e
             (log/error ::error-telegram-consumer e))))))

(defmethod ig/halt-key! ::telegram-consumer
  [_ _routes]
  (log/info :stopping ::telegram-consumer))
