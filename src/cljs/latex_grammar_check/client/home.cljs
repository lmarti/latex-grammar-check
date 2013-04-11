(ns latex-grammar-check.client.home
  (:require [dommy.core :refer [listen! replace-contents! append!]]
            [dommy.attrs :refer [toggle-class! show! hidden?]]
            [dommy.template :as template]
            [domina :as dom]
            [ajax.core :as ajax])
  (:require-macros [dommy.macros :refer [sel1]]))

(def editor (atom nil))

(def container (sel1 :#container))

(defn create-editor [text-area opts]
  (.fromTextArea js/CodeMirror text-area (clj->js opts)))

(defn handle-grammar-check-result [result]
  (replace-contents! (sel1 :#check-grammar-result)
                     (map #(template/node [:li (:message %)]) result)))

(defn handle-check-grammar [e]
  (.save @editor)
  (ajax/POST "/check-grammar"
             {:handler handle-grammar-check-result
              :params  {:latex-markup  (dom/value (sel1 :#latex-markup))}}))

(defn ^:export init []
  (-> container
      (append! (template/node [:textarea#latex-markup "A sentence with a error in the Hitchhiker's Guide tot he Galaxy"]))
      (append! (template/node [:button#check-grammar "Check Grammar"]))
      (append! (template/node [:lu#check-grammar-result])))
  (reset! editor (create-editor (sel1 :#latex-markup) {:lineNumbers true
                                                       :mode { :name "stex" }
                                                       :tabMode "indent"}))  
  (listen! (sel1 :#check-grammar) :click handle-check-grammar))