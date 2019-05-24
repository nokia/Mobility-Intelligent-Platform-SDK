#
# Be sure to run `pod lib lint MoveInSaclaySDK.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see https://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
  s.name             = 'MoveInSaclaySDK'
  s.version          = '0.2.0'
  s.summary          = 'MoveInSaclaySDK.'
  s.homepage         = 'https://github.com/nokia/Mobility-Intelligent-Platform-SDK.git/ios_pod'
  s.license          = { :type => 'BSD', :file => 'LICENSE' }
  s.author           = { 'Bell Labs' => 'support@moveinsaclay.com' }
  s.source           = { :git => 'https://github.com/nokia/Mobility-Intelligent-Platform-SDK.git', :tag => s.version.to_s }
  s.ios.deployment_target = '10.0'
  s.swift_version = '4.2'
  s.source_files = 'MoveInSaclaySDK/**/*.{h,m,swift,mlmodel}'
  s.static_framework = true
  s.resource_bundles = {
    'MoveInSaclaySDKBundle' => ['MoveInSaclaySDK/**/*.{png,storyboard,xib,xcassets}', 'MoveInSaclaySDK/Resources/*.{plist,bundle,json}']
  }

  s.frameworks = 'UIKit'
  s.public_header_files = ['MoveInSaclaySDK/LocoKit/Framework/LocoKitCore.framework/Headers/LocoKitCore-Swift.h',
                      'MoveInSaclaySDK/LocoKit/Framework/LocoKitCore.framework/Headers/LocoKitCore.h',
                      'MoveInSaclaySDK/ApiGateWay/AWSAPI_OZWRAU7P77_WalletManagerMobileHubClient.h',
                      'MoveInSaclaySDK/ApiGateWay/AWSAPI_POG7I433NJ_MotionMobileHubClient.h',
                      'MoveInSaclaySDK/ApiGateWay/AWSAPI_1M748BA2PH_MobilityProfilMobileHubClient.h'
                      ]
                      
  s.vendored_frameworks = ['MoveInSaclaySDK/LocoKit/Framework/LocoKitCore.framework']
  s.preserve_paths = ['MoveInSaclaySDK/LocoKit/Framework/LocoKitCore.framework/Modules/module.modulemap', 'MoveInSaclaySDK/**/*.{mlmodel}']
  
  s.dependency 'SwiftNotes', '~> 1.1.0'
  s.dependency 'AWSAuthCore', '~> 2.9.7'
  s.dependency 'AWSAuthUI', '~> 2.9.7'
  s.dependency 'AWSUserPoolsSignIn', '~> 2.9.7'
  s.dependency 'AWSAPIGateway', '~> 2.9.7'
  s.dependency 'AWSMobileClient', '~> 2.9.7'
  s.dependency 'AWSDynamoDB', '~> 2.9.7'
  s.dependency 'Mapbox-iOS-SDK', '~> 4.10.0'
  s.dependency 'MapboxNavigation', '~> 0.32.0'
  s.dependency 'MapboxGeocoder.swift', '~> 0.11.0'
  s.dependency 'AppCenter', '~> 2.0.1'
  s.dependency 'Charts', '~> 3.2.0'
  s.dependency 'GRDB.swift', '~> 3.7.0'
  s.dependency 'Upsurge', '~> 0.10.2'

end
