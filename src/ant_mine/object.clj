(ns ant-mine.object)

(defprotocol
  actor
  (act [this world])
  (react [this world event]))
