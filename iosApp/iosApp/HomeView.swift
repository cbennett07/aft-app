import SwiftUI

struct HomeView: View {
    @State private var selectedGender: Gender = .male
    @State private var age: Double = 25
    @State private var selectedMosCategory: MosCategory = .combat
    @State private var showCalculator = false

    var body: some View {
        ZStack {
            // Background gradient
            LinearGradient(
                colors: [Color.armyBlack, Color.armyDarkGray],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            VStack(spacing: 0) {
                ScrollView {
                    VStack(spacing: 24) {
                        // Header
                        VStack(spacing: 8) {
                            Text("ARMY")
                                .font(.system(size: 36, weight: .black))
                                .tracking(8)
                                .foregroundColor(.armyGold)
                            Text("FITNESS TEST")
                                .font(.system(size: 28, weight: .bold))
                                .tracking(4)
                                .foregroundColor(.white)
                            Text("CALCULATOR")
                                .font(.system(size: 24, weight: .medium))
                                .tracking(2)
                                .foregroundColor(.white.opacity(0.8))

                            // Effective date badge
                            Text("EFFECTIVE JUNE 2025")
                                .font(.system(size: 10, weight: .bold))
                                .tracking(1)
                                .foregroundColor(.armyGold)
                                .padding(.horizontal, 12)
                                .padding(.vertical, 6)
                                .background(Color.armyGold.opacity(0.2))
                                .overlay(
                                    RoundedRectangle(cornerRadius: 4)
                                        .stroke(Color.armyGold, lineWidth: 1)
                                )
                                .padding(.top, 8)
                        }
                        .padding(.top, 40)
                        .padding(.bottom, 20)

                        // Soldier Information Card
                        VStack(alignment: .leading, spacing: 20) {
                            Text("SOLDIER INFORMATION")
                                .font(.system(size: 14, weight: .bold))
                                .tracking(1)
                                .foregroundColor(.armyGold)

                            // Age
                            VStack(alignment: .leading, spacing: 4) {
                                Text("AGE: \(Int(age))")
                                    .font(.system(size: 16, weight: .medium))
                                    .foregroundColor(.white)

                                Slider(value: $age, in: 17...70, step: 1)
                                    .accentColor(.armyGold)
                            }

                            // Gender
                            VStack(alignment: .leading, spacing: 8) {
                                Text("GENDER")
                                    .font(.system(size: 14, weight: .medium))
                                    .foregroundColor(.white)

                                HStack(spacing: 12) {
                                    GenderButton(
                                        title: "MALE",
                                        isSelected: selectedGender == .male,
                                        action: { selectedGender = .male }
                                    )
                                    GenderButton(
                                        title: "FEMALE",
                                        isSelected: selectedGender == .female,
                                        action: { selectedGender = .female }
                                    )
                                }
                            }

                            // MOS Category
                            VStack(alignment: .leading, spacing: 8) {
                                Text("MOS CATEGORY")
                                    .font(.system(size: 14, weight: .medium))
                                    .foregroundColor(.white)

                                VStack(spacing: 8) {
                                    MosCategoryButton(
                                        title: "COMBAT MOS",
                                        subtitle: "Min: 60/event \u{2022} 350 total",
                                        isSelected: selectedMosCategory == .combat,
                                        action: { selectedMosCategory = .combat }
                                    )
                                    MosCategoryButton(
                                        title: "COMBAT-ENABLING MOS",
                                        subtitle: "Min: 60/event \u{2022} 300 total",
                                        isSelected: selectedMosCategory == .combatEnabling,
                                        action: { selectedMosCategory = .combatEnabling }
                                    )
                                }
                            }
                        }
                        .padding(20)
                        .background(Color.white.opacity(0.05))
                        .cornerRadius(12)

                        Spacer(minLength: 16)

                        // Continue Button
                        Button(action: {
                            showCalculator = true
                        }) {
                            Text("ENTER EVENT SCORES")
                                .font(.system(size: 16, weight: .bold))
                                .tracking(1)
                                .foregroundColor(.armyBlack)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 16)
                                .background(Color.armyGold)
                                .cornerRadius(8)
                        }

                        Text("Based on official HQDA EXORD 218-25 scoring tables")
                            .font(.system(size: 11))
                            .foregroundColor(.white.opacity(0.5))
                            .multilineTextAlignment(.center)

                        Spacer(minLength: 40)
                    }
                    .padding(.horizontal, 16)
                }
            }
        }
        .fullScreenCover(isPresented: $showCalculator) {
            CalculatorView(
                gender: selectedGender,
                age: Int32(age),
                mosCategory: selectedMosCategory,
                onDismiss: { showCalculator = false }
            )
        }
    }
}

struct GenderButton: View {
    let title: String
    let isSelected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.system(size: 14, weight: isSelected ? .bold : .medium))
                .tracking(1)
                .foregroundColor(isSelected ? .armyBlack : .white)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 12)
                .background(isSelected ? Color.armyGold : Color.white.opacity(0.1))
                .cornerRadius(8)
                .overlay(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(isSelected ? Color.armyGold : Color.white.opacity(0.3), lineWidth: 1)
                )
        }
    }
}

struct MosCategoryButton: View {
    let title: String
    let subtitle: String
    let isSelected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(title)
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(isSelected ? .armyGold : .white)
                    Text(subtitle)
                        .font(.system(size: 11))
                        .foregroundColor(.white.opacity(0.6))
                }
                Spacer()
                if isSelected {
                    ZStack {
                        Circle()
                            .fill(Color.armyGold)
                            .frame(width: 24, height: 24)
                        Text("\u{2713}")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(.armyBlack)
                    }
                }
            }
            .padding()
            .background(isSelected ? Color.armyGold.opacity(0.15) : Color.clear)
            .cornerRadius(8)
            .overlay(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(isSelected ? Color.armyGold : Color.white.opacity(0.2), lineWidth: 1)
            )
        }
    }
}

#Preview {
    HomeView()
}
