(ns latex-grammar-check.client.home
  (:require [dommy.core :refer [listen! replace-contents! append! descendant?]]
            [dommy.attrs :refer [toggle-class! hidden? toggle!]]
            [dommy.template :as template]
            [domina :as dom]
            [ajax.core :as ajax]
            [latex-grammar-check.client.codemirror :as cm]
            [latex-grammar-check.client.model :as model]
            [latex-grammar-check.client.text-mark :as tm]
            [latex-grammar-check.client.error-message :as em])
  (:require-macros [dommy.macros :refer [sel1 sel]]))

(def editor (atom nil))

(def container (sel1 :#container))

(def grammar-check-marks (atom []))

(defn add-index [errors]
  (->> errors
       (map-indexed (fn [index error] (assoc error :index index)))
       vec))

(defn grammar-error-tooltip [message replacements]
  [:div
   [:lu
    (for [[index r] (map-indexed vector replacements)]
     [:li {:id index :classes ["replacement"]} r]
      )]])

(defn grammar-error [text]
  [:span {:classes ["grammar-checker-problem"]} text])

(defn apply-replacement-fn [mark]
  (fn [replacement]
    (let [{from "from" to "to"} (js->clj (cm/find-mark mark))]
      (cm/replace-range @editor replacement from to))))

(defn attach-popover [element replacements mark]
  (listen! element :mouseover #(do (.popover (js/jQuery element) "show")
                                 (let [popover (sel1 :.popover)
                                       items (sel popover :.replacement)
                                       f (apply-replacement-fn mark)]
                                   (doseq [item items]
                                     (let [id (.-id item)]
                                       (listen! item :click (fn [e] 
                                                              (f (get replacements id))
                                                              (.popover (js/jQuery element) "hide")))))
                                   (listen! popover :mouseout (fn [e] (do (when-not (descendant? (.-relatedTarget e) popover) 
                                                                            (.popover (js/jQuery element) "hide"))))))))
  mark)

(defn handle-grammar-check-result-old [coll]
  (replace-contents! (sel1 :#check-grammar-result)
                     (template/node
                        [:div
                          [:tr  [:td "##"] [:td [:b "Description"]]]
                          [:tbody 
                           (map (fn [r] [:tr [:td (:line r)] [:td (:message r)]]) coll)]]))
  
  (doseq [mark @grammar-check-marks] (cm/clear-mark mark))
  (reset! grammar-check-marks [])
  
  ;(.setGutterMarker @editor 1 "grammar-checker-problem-gutter" (template/node [:span "x"]))
  
  (doseq [r coll]
    (let [{:keys [line column end-line end-column message suggested-replacements]} r
          from {:line line :ch (dec column)}
          to {:line end-line :ch (dec end-column)}
          text (cm/get-range @editor from to)
          tooltip (template/node (grammar-error-tooltip message suggested-replacements))
          element (template/node (grammar-error text))]
      (.popover (js/jQuery element) (clj->js {:container "body"
                                              :title message 
                                              :content (.-outerHTML tooltip)
                                              :trigger "manual"
                                              :html true
                                              :placement "bottom"}))
      
      (->> (cm/mark-text @editor from to
                         {:clearOnEnter true
                          :replacedWith element
                          })
           (attach-popover element suggested-replacements)
           (swap! grammar-check-marks conj)
           )
      )))

(defn handle-grammar-check-result [errors]
  (model/remove-all-errors)
  (doseq [error (add-index errors)]
    (model/add-error error)))

(defn handle-check-grammar [e]
  (cm/focus @editor)
  (ajax/POST "/check-grammar"
             {:handler handle-grammar-check-result
              :params  {:latex-markup (cm/get-value @editor)}}))

(defn handle-dumb-check-grammar [e]
  (cm/focus @editor)
  (ajax/POST "/dumb-check-grammar"
             {:handler handle-grammar-check-result
              :params  {:latex-markup (cm/get-value @editor)}}))

(defn handle-extract-text-result [text]
  (cm/set-value @editor text))

(defn handle-extract-text [e]
  (cm/focus @editor)
  (ajax/POST "/extract-text"
             {:handler handle-extract-text-result
              :params  {:latex-markup (cm/get-value @editor)}}))

(defn ^:export init []
  (-> container 
      (append! (template/node [:textarea#latex-markup "A \\LaTeX sentence with \\emph{a} error in the \\textbf{Hitchhiker's Guide} tot he Galaxy"]))
      (append! (template/node 
       [:div#buttons.text-center
        [:div#buttons.btn-group
          [:a#check-grammar.btn  
            [:i.icon-check]
            [:span  " Check Grammar"]]
          [:a#dumb-check-grammar.btn "Dumb Check Grammar"]
          [:a#extract-text.btn "Extract Text"]]]))
      (append! (template/node [:div#check-grammar-result])))
  (reset! editor (cm/create-editor (sel1 :#latex-markup) {:lineNumbers true
                                                          :mode { :name "stex" }
                                                          :tabMode "indent"
                                                          :highlightSelectionMatches true
                                                          ;;:styleActiveLine true
                                                          :gutters ["grammar-checker-problem-gutter"]}))  
  (listen! (sel1 :#check-grammar) :click handle-check-grammar)
  (listen! (sel1 :#dumb-check-grammar) :click handle-dumb-check-grammar)
  (listen! (sel1 :#extract-text) :click handle-extract-text)
  (em/init)
  (tm/init @editor))