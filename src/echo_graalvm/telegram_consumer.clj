(ns echo-graalvm.telegram
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]))

(defmethod ig/init-key ::config
  [_ {:keys [path env overrides]}]
  (log/info :starting ::config))

(defmethod ig/halt-key! ::config
  [_ _routes]
  (log/info :stopping ::config))
