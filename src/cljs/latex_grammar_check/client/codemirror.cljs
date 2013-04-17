(ns latex-grammar-check.client.codemirror)

(defn create-editor [text-area opts]
  (.fromTextArea js/CodeMirror text-area (clj->js opts)))

(defn get-value 
  ([editor] (.getValue editor))
  ([editor separator] (.getValue editor separator)))

(defn set-value [editor value] 
  (.setValue editor value))

(defn focus [editor] 
  (.focus editor))

(defn mark-text [editor from to options] 
  (.markText editor
            (clj->js from)
            (clj->js to)
            (clj->js options)))

(defn clear-mark [editor mark]
  (.clear mark))

(defn get-range [editor from to]
  (.getRange editor (clj->js from) (clj->js to)))


