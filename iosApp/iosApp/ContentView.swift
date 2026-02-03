//
//  ContentView.swift
//  iosApp
//
//  Created by cab on 2/1/26.
//

import SwiftUI
                                                                                 
  struct ContentView: View {
      let calculator = AftCalculator()
                                                                                 
      var body: some View {
          VStack {
              Text("AFT Calculator")
                  .font(.largeTitle)
                  .fontWeight(.bold)
                                                                                 
              Text("iOS App Coming Soon")
                  .foregroundColor(.gray)
          }
          .padding()
      }
  }
                                                                                 
  #Preview {
      ContentView()
  }                        
