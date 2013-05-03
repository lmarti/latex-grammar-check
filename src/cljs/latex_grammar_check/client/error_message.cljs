(ns latex-grammar-check.client.error-message
  (:require [latex-grammar-check.client.model :as model]
            [dommy.core :refer [listen! replace-contents! append! descendant? remove!]]
            [dommy.attrs :refer [toggle-class! hidden? toggle!]]
            [dommy.template :as template])
  (:require-macros [dommy.macros :refer [sel1]]))

(def error-to-message-map (atom {}))

(defn error-message [error]
  (template/node [:tr [:td (:line error)] [:td (:message error)]]))

(defn handle-add [error]
  (let [parent (sel1 :#check-grammar-result)
        message (error-message error)]
    (when (empty? @error-to-message-map)
      (append! parent (template/node
        [:table#messages_table {:class "table table-condensed table-striped table-hover"}
         [:tr [:td "##"] [:td [:b "Description"]]]
         [:tbody#messages]])))
    (append! (sel1 :#messages) message)
    (listen! message :click #(model/select-error error))
    (swap! error-to-message-map assoc error message)))

(defn handle-remove [error]
  (when-let [message (get @error-to-message-map error)]
    (remove! message)
    (swap! error-to-message-map dissoc error)
    (when (empty? @error-to-message-map)
      (remove! (sel1 :#messages_table)))))

(defn handle-select [error]
  )

(defn init []
  (add-watch model/action :error-message-watcher
    (fn [_ _ _ {type :type error :data}]
      (case type
        :add (handle-add error)
        :remove (handle-remove error)
        :select (handle-select error)))))
