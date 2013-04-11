(ns latex-grammar-check.util
  (:import [org.languagetool JLanguageTool]
           [org.languagetool.language BritishEnglish]))

;tbd: rename namespace to language-tool
(defn check-grammar [text]
  (let [lang-tool (JLanguageTool. (BritishEnglish.))]
    (.activateDefaultPatternRules lang-tool)
    (let [matches (.check lang-tool text)]
      (map (fn [m] {:line (.getLine m)
                    :end-line (.getEndLine m)
                    :column (.getColumn m)
                    :end-column (.getEndColumn m)
                    :message (.getMessage m)
                    :suggested-replacements (vec (.getSuggestedReplacements m))}) matches))))

;(check-grammar "A sentence with a error in the Hitchhiker's Guide tot he Galaxy")
