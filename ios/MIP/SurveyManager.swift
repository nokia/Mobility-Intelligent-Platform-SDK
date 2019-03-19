import Foundation

class SurveyManager {
    static func loadSurvey (surveyWeb : UIWebView) {
        let myURL = URL(string: "https://fr.surveymonkey.com/r/PRPGX98")
        let myRequest = URLRequest(url: myURL!)
        surveyWeb.loadRequest(myRequest)
    }
}
