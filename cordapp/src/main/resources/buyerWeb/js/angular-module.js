"use strict";

// --------
// WARNING:
// --------

// THIS CODE IS ONLY MADE AVAILABLE FOR DEMONSTRATION PURPOSES AND IS NOT SECURE!
// DO NOT USE IN PRODUCTION!

// FOR SECURITY REASONS, USING A JAVASCRIPT WEB APP HOSTED VIA THE CORDA NODE IS
// NOT THE RECOMMENDED WAY TO INTERFACE WITH CORDA NODES! HOWEVER, FOR THIS
// PRE-ALPHA RELEASE IT'S A USEFUL WAY TO EXPERIMENT WITH THE PLATFORM AS IT ALLOWS
// YOU TO QUICKLY BUILD A UI FOR DEMONSTRATION PURPOSES.

// GOING FORWARD WE RECOMMEND IMPLEMENTING A STANDALONE WEB SERVER THAT AUTHORISES
// VIA THE NODE'S RPC INTERFACE. IN THE COMING WEEKS WE'LL WRITE A TUTORIAL ON
// HOW BEST TO DO THIS.

const app = angular.module('demoAppModule', ['ui.bootstrap', 'naif.base64']);

// Fix for unhandled rejections bug.
app.config(['$qProvider', function ($qProvider) {
    $qProvider.errorOnUnhandledRejections(false);
}]);

app.controller('DemoAppController', function($scope, $http, $window, $rootScope, $location, $uibModal) {

    //FILEUPLOAD
   $scope.onChange = function (e, fileList) {
     //alert('this is on-change handler!');
   };

   $scope.onLoad = function (e, reader, file, fileList, fileOjects, fileObj) {

   };

   var uploadedCount = 0;

   $scope.files = [];

   $scope.fileValidationOK = false;
   $scope.fileValidationKO = false;
   $scope.fileErrors = false;

   $scope.SendData = function (file) {

        $scope.fileValidationOK = false;
        $scope.fileValidationKO = false;
        $scope.fileErrors = false;

        if(file.filesize > 200000) {
            $scope.fileErrors = true;
            $scope.fileValidationErrorMessage = 'Max. file size 200KB';
            return;
        }

        if( !file.filename.toLowerCase().includes(".pdf")) {
            $scope.fileErrors = true;
            $scope.fileValidationErrorMessage = 'File extension not allowed!! PDF file required.';
            return;
        }

       // use $.param jQuery function to serialize data from JSON
        var data = {
            "base64file" : file.base64
        };

        var config = {
            headers : {
                'Content-Type': 'text/plain;'
            }
        }

        $http.post(apiBaseURL + "validate-doc/", data, config)
        .then(
               function(response){
                 $scope.fileValidationOK = true;
               },
               function(response){
                 $scope.fileValidationKO = true;
                 $scope.fileValidationErrorMessage = 'Validation KO';
               }
        );
   };

    //END FILEUPLOAD

    const demoApp = this;

    // We identify the node.
    const apiBaseURL = "/api/cryptofishy/";
    demoApp.cryptofishies = [];

    // This Node
    $http.get(apiBaseURL + "me").then((response) => {
        demoApp.thisNode = response.data.me;
    });

    //Function to get the CryptoFishies
    demoApp.getCryptoFishies = () => $http.get(apiBaseURL + "cryptofishies")
              .then((response) => demoApp.cryptofishies = Object.keys(response.data)
              .map((key) => response.data[key].state.data));

     //Getting the CryptoFishies
     demoApp.getCryptoFishies();

     //Get the results stored in the DLT
     demoApp.getCertificateList = () => $http.get(apiBaseURL + "certificates/")
              .then((response) => demoApp.resultsCertificateList = Object.keys(response.data)
              .map((key) => response.data[key].state.data));

    //Get the CryptoFishyCertificates list
    demoApp.getCertificateList();

    // Download the CryptoFishy certificate
    demoApp.showCertificateInfo = (id) => {
            window.open(apiBaseURL + "download-doc?id=" + id, "_self");
    };

    demoApp.refresh = () => {
            demoApp.getCryptoFishies();
            demoApp.getCertificateList();
    };

});

