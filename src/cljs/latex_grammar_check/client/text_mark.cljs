(ns latex-grammar-check.client.text-mark
  (:require [dommy.core :refer [listen! replace-contents! append! descendant?]]
            [dommy.attrs :refer [toggle-class! hidden? toggle!]]
            [dommy.template :as template]
            [domina :as dom]
            [ajax.core :as ajax]
            [latex-grammar-check.client.codemirror :as cm]
            [latex-grammar-check.client.model :as model])
  (:require-macros [dommy.macros :refer [sel1 sel]]))

(def text-marks (atom []))

(defn grammar-error [text]
  [:span {:classes ["grammar-checker-problem"]} text])

(defn append-text-mark! [editor error]
  (let [{:keys [line column end-line end-column message replacements]} error
          from {:line line :ch column}
          to {:line end-line :ch end-column}
          text (cm/get-range editor from to)
          element (template/node (grammar-error text))]
      (->> (cm/mark-text editor from to
                         {:clearOnEnter true
                          :replacedWith element
                          })
           (swap! text-marks conj))))

(defn remove-text-mark! [editor error]
  )

(defn clear-text-marks! [editor]
  (doseq [mark @text-marks]
    (cm/clear-mark mark))
  (reset! text-marks []))

(defn init[editor]
  (add-watch model/action :text-mark-watcher
    (fn [_ _ _ {:keys [type data]}]
      (case type
        :add (append-text-mark! editor data)
        :remove (remove-text-mark! editor data)
        :clear (clear-text-marks! editor)))))
