package data.boilerplate.learning

class LearningModel() extends Serializable {

}

class LearningParams{
  var lrate = 0.001
  var windowLength = 1000

  var features:FeatureParams = null

}

class FeatureParams{
  var extractCSS = true
  var extractAttributes = true
  var wordDensity = true
  var linkDensity = true
  var extractID = true


}
