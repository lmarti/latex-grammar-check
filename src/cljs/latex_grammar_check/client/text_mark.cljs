(ns latex-grammar-check.client.text-mark
  (:require [dommy.core :refer [listen! replace-contents! append! descendant? live-listener]]
            [dommy.attrs :refer [toggle-class! hidden? toggle! style]]
            [dommy.template :as template]
            [ajax.core :as ajax]
            [latex-grammar-check.client.codemirror :as cm]
            [latex-grammar-check.client.model :as model]
            [jayq.util :refer [wait log]])
  (:require-macros [dommy.macros :refer [sel1 sel deftemplate]]))

(def error-to-mark-map (atom {}))


(deftemplate popover-content [replacements]
  [:div
   [:lu#replacements
    (for [[index r] (map-indexed vector replacements)]
     [:li {:id index} r])]])

(defn attach-popover! [elem message replacements]
  (let [content (popover-content replacements)]
    (.popover (js/jQuery elem) 
              (clj->js {:container "body"
                        :title message 
                        :content (.-outerHTML content)
                        :trigger "manual"
                        :html true
                        :placement "bottom"}))))

(defn grammar-error [text]
  [:span.grammar-checker-problem text])

;; tbd: consider moving to handle-mouseover-error
(def mouseover-popover (atom false))

(defn show-popover [elem]
  (.popover (js/jQuery elem) "show")
  (sel1 :.popover))

(defn hide-popover [elem]
  (reset! mouseover-popover false)
  (.popover (js/jQuery elem) "hide"))

(defn handle-mouseleave-error [elem]
  (fn [e]
    (wait 500 #(when-not @mouseover-popover (hide-popover elem)))))

(defn replacement [editor mark replacement]
  (let [{from "from" to "to"} (js->clj (cm/find-mark mark))]
      (cm/replace-range editor replacement from to)))

(defn replacement-selection-handler [elem editor error mark e]
  (let [target (.-target e) 
        id (.-id target)]
    (replacement editor mark (get-in error [:replacements id]))
    (hide-popover elem)
    (.clear mark)))

(defn attach-replacement-selection-handler! [elem popover editor error mark handler]
  (listen! (sel1 popover "#replacements") :click #(handler elem editor error mark %)))

;; tbd use events to signal user mousing over a grammatical error
(defn handle-mouseover-error [editor error mark]
  (fn [e] 
    (this-as elem
      (let [popover (show-popover elem)]
        (attach-replacement-selection-handler! 
           elem popover editor error mark replacement-selection-handler)
        (listen! elem :mouseleave (handle-mouseleave-error elem))
        (listen! popover :mouseenter #(reset! mouseover-popover true))
        (listen! popover :mouseleave #(hide-popover elem))
        ))))

(defn append-text-mark! [editor error]
  (let [{:keys [line column end-line end-column message replacements]} error
        from {:line line :ch column}
        to {:line end-line :ch end-column}
        text (cm/get-range editor from to)
        elem (template/node (grammar-error text))
        mark (cm/mark-text editor from to {:clearOnEnter true :replacedWith elem})]
    (attach-popover! elem message replacements)
    (listen! elem :mouseover (handle-mouseover-error editor error mark))
    mark))

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
