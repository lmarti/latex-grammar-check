(ns latex-grammar-check.client.error-message
  (:require [latex-grammar-check.client.model :as model]
            [dommy.core :refer [listen! replace-contents! append! descendant? remove!]]
            [dommy.attrs :refer [toggle-class! hidden? toggle!]]
            [dommy.template :as template])
  (:require-macros [dommy.macros :refer [sel1]]))

(defn error-id [{:keys [index] :as error}]
  (str "error-" index))

(defn append-error-message! [error]
  (append! (sel1 :#errors)
           (template/node [:tr {:id (error-id error)} 
                           [:td (:line error)] 
                           [:td (:message error)]])))

(defn remove-error-message! [error]
  (remove! (sel1 (error-id error))))

(defn clear-error-messages! []
  (replace-contents! (sel1 :#errors) nil))

(defn init[]
  (add-watch model/action :error-message-watcher
    (fn [_ _ _ {:keys [type data]}]
      (case type
        :add (append-error-message! data)
        :remove (remove-error-message! data)
        :clear (clear-error-messages!)))))