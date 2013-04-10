(ns latex-grammar-check.client.home
  (:require [dommy.core :refer [listen! replace-contents! append!]]
            [dommy.attrs :refer [toggle-class! show! hidden?]]
            [dommy.template :as template]
            [domina :as dom]
            [ajax.core :as ajax])
  (:require-macros [dommy.macros :refer [sel1]]))

(def container (sel1 :#container))

(defn append-editor! [parent opts]
  (let [text-area (template/node [:textarea#latex-markup "put some latex here to see it pretty printed"])]
    (append! parent text-area)
    (.fromTextArea js/CodeMirror text-area (clj->js opts))
    parent))

(defn handle-check-grammar [e]
  (ajax/POST "/check-grammar"
             {:handler (fn [response] (js/alert response))
              :params  {:latex-markup (dom/value (sel1 :#latex-markup))}}))

(defn ^:export init []
  (-> container
      (append-editor! {:lineNumbers true
                       :mode { :name "stex" }
                       :tabMode "indent"})
      (append! (template/node [:button#check-grammar "Check Grammar"])))
  (listen! (sel1 :#check-grammar) :click handle-check-grammar))