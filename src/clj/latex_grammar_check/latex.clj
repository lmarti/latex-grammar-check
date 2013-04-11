(ns latex-grammar-check.latex
  (:require [clojure.string :as string])
  (:import [texparser.parser LatexLexer LatexWordCounter]
           [java.io PushbackReader StringReader]
           [texparser.node EOF TCpart TArgument TWord TWhitespace]))
  
(defn eof? [token]
  (instance? EOF token))
  
;AG: improved structure, and group text in sections
(defn extract-text [latex-markup]
  (let [lexer (LatexLexer. (PushbackReader. (StringReader. latex-markup) 4096))]
    (loop [token (.next lexer) 
           result []]
      (if (eof? token)
        (string/join " " result)
        (let [text (condp instance? token
                      TWord (.getText token)
                      nil)]
          (recur (.next lexer) (if text (conj result text) result)))
        
        ))))
 
 
; (case (class token)
;          TArgument (println (.getText token))
;          (println "No Match"))
 
;(extract-text (slurp "src/sablecc/sample.tex"))
 
