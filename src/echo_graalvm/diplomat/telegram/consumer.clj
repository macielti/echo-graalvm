(ns echo-graalvm.diplomat.telegram.consumer
  (:require [echo-graalvm.telegram-consumer :as component.telegram-consumer]
            [schema.core :as s]))

(s/defn echo-message!
  [message :- s/Str
   chat-id
   {:keys [telegram-token]}
   http-client]
  (component.telegram-consumer/send-text! telegram-token chat-id (str "Echoing: " message) http-client))

(defn main-handler-fn!
  [update
   components]
  (echo-message! (-> update :message :text)
                 (-> update :message :chat :id)
                 (:config components)
                 (:http-client components)))
