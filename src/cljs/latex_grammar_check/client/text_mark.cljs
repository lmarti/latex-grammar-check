(ns latex-grammar-check.client.text-mark
  (:require [dommy.core :refer [listen! replace-contents! append! descendant?]]
            [dommy.attrs :refer [toggle-class! hidden? toggle!]]
            [dommy.template :as template]
            [domina :as dom]
            [ajax.core :as ajax]
            [latex-grammar-check.client.codemirror :as cm]
            [latex-grammar-check.client.model :as model])
  (:require-macros [dommy.macros :refer [sel1 sel]]))

(def error-to-mark-map (atom {}))

(defn grammar-error [text]
  [:span {:classes ["grammar-checker-problem"]} text])

(defn append-text-mark! [editor error]
  (let [{:keys [line column end-line end-column message replacements]} error
        from {:line line :ch column}
        to {:line end-line :ch end-column}
        text (cm/get-range editor from to)
        element (template/node (grammar-error text))]
    (cm/mark-text editor from to {:clearOnEnter true :replacedWith element})))

(defn handle-add [editor error]
  (let [mark (append-text-mark! editor error)
        clear-handler #(model/remove-error error)]
    (cm/on mark :clear clear-handler)
    (swap! error-to-mark-map assoc error {:mark mark :clear-handler clear-handler})))

(defn handle-remove [editor error]
  (when-let [{:keys [mark clear-handler]} (get @error-to-mark-map error)]
    (cm/off mark :clear clear-handler)
    (cm/clear-mark mark)
    (swap! error-to-mark-map dissoc error)))

(defn handle-select [editor error]
  (when-let [{:keys [mark]} (get @error-to-mark-map error)]
    (let [{from "from"} (js->clj (.find mark))]
      (.setCursor editor (clj->js from))))
  (cm/focus editor))

(defn init [editor]
  (add-watch model/action :text-mark-watcher
    (fn [_ _ _ {type :type error :data}]
      (case type
        :add (handle-add editor error)
        :remove (handle-remove editor error)
        :select (handle-select editor error)))))
