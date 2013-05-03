(ns latex-grammar-check.client.alert-message
  (:require [dommy.core :refer [append! remove!]]
            [dommy.template :as template])
  (:require-macros [dommy.macros :refer [sel1 deftemplate]]))


(deftemplate alert [msg sub-class]
  [:div#alert {:class (str "alert " sub-class)}
    [:button {:type "button" :class "close" :data-dismiss "alert"} "x"]
    ;<button type="button" class="close" data-dismiss="alert">&times;</button>
    msg])


(defn show-info-alert! [msg]
  (append! (sel1 :#alert-message-container)
           (alert msg "alert-info")))

(defn show-success-alert! [msg]
  (append! (sel1 :#alert-message-container)
           (alert msg "alert-success")))

(defn hide-alert! []
  (when-let [elem (sel1 :#alert)]
    (remove! elem)))

;(show-info-alert! "daa")

;(hide-alert!)