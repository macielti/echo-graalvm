(ns echo-graalvm.diplomat.telegram.consumer
  (:require [telegrama.api :as api]))

(defn handle-test!
  [{:keys [event components]}]
  (let [token (-> components :config :telegram :token)
        chat-id (-> event :identification :id)]
    (api/send-text! token chat-id "Tested!" (:http-client components))))

(defn handle-chat-id!
  [{:keys [event components]}]
  (let [token (-> components :config :telegram :token)
        chat-id (-> event :identification :id)
        message (str "Your chat-id is: " chat-id)]
    (api/send-text! token chat-id message (:http-client components))))

(def settings {:bot-command {:test    {:handler handle-test!}
                             :chat-id {:handler handle-chat-id!}}})
