(ns latex-grammar-check.client.bootstrap
  (:require [dommy.core :refer [listen! replace-contents! append! descendant?]]
            [dommy.attrs :refer [toggle-class! hidden? toggle!]]
            [dommy.template :as template]
            [ajax.core :as ajax])
  (:require-macros [dommy.macros :refer [sel1 deftemplate]]))

(deftemplate dropup [items]
  [:div {:class "btn-group dropup"}
   [:button {:class "btn primary"} (first items)]
   [:button {:class "btn primary dropdown-toggle" :data-toggle "dropdown"}
    [:span.caret]]
   [:ul {:class "dropdown-menu pull-right"}
    (for [item items] [:li item])]])




