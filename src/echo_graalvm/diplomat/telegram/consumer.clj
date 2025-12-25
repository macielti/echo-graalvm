(ns echo-graalvm.diplomat.telegram.consumer
  (:require [telegrama.api :as api]))

(defn handle-help!
  [{:keys [event components]}]
  (let [token (-> components :config :telegram :token)
        chat-id (-> event :identification :id)
        message (str "• /help — Show this list of commands. \n• /test — Check if the bot is active and responsive. \n• /chat-id — Reveal your unique Telegram Chat ID.")]
    (api/send-text! token chat-id message (:http-client components))))

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

(def settings {:bot-command {:help    {:handler handle-help!}
                             :test    {:handler handle-test!}
                             :chat-id {:handler handle-chat-id!}}})
