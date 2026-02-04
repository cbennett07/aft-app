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

            ScrollView {
                VStack(spacing: 24) {
                    // Header
                    VStack(spacing: 8) {
                        Text("AFT")
                            .font(.system(size: 48, weight: .black))
                            .foregroundColor(.armyGold)
                        Text("ARMY FITNESS TEST")
                            .font(.system(size: 14, weight: .bold))
                            .tracking(4)
                            .foregroundColor(.white.opacity(0.8))
                        Text("CALCULATOR")
                            .font(.system(size: 24, weight: .bold))
                            .tracking(2)
                            .foregroundColor(.white)

                        // Effective date tagline
                        Text("EFFECTIVE JUNE 2025")
                            .font(.system(size: 10, weight: .bold))
                            .tracking(1)
                            .foregroundColor(.armyGold)
                            .padding(.horizontal, 12)
                            .padding(.vertical, 6)
                            .overlay(
                                RoundedRectangle(cornerRadius: 4)
                                    .stroke(Color.armyGold, lineWidth: 1)
                            )
                            .padding(.top, 8)
                    }
                    .padding(.top, 40)
                    .padding(.bottom, 20)

                    // Gender Selection
                    VStack(alignment: .leading, spacing: 12) {
                        Text("GENDER")
                            .font(.system(size: 12, weight: .bold))
                            .tracking(1)
                            .foregroundColor(.armyGold)

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
                    .padding()
                    .background(Color.white.opacity(0.05))
                    .cornerRadius(12)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color.white.opacity(0.1), lineWidth: 1)
                    )

                    // Age Input
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            Text("AGE")
                                .font(.system(size: 12, weight: .bold))
                                .tracking(1)
                                .foregroundColor(.armyGold)
                            Spacer()
                            Text("\(Int(age))")
                                .font(.system(size: 24, weight: .bold))
                                .foregroundColor(.white)
                        }

                        Slider(value: $age, in: 17...62, step: 1)
                            .accentColor(.armyGold)

                        HStack {
                            Text("17")
                                .font(.system(size: 11))
                                .foregroundColor(.white.opacity(0.5))
                            Spacer()
                            Text("62+")
                                .font(.system(size: 11))
                                .foregroundColor(.white.opacity(0.5))
                        }
                    }
                    .padding()
                    .background(Color.white.opacity(0.05))
                    .cornerRadius(12)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color.white.opacity(0.1), lineWidth: 1)
                    )

                    // MOS Category Selection
                    VStack(alignment: .leading, spacing: 12) {
                        Text("MOS CATEGORY")
                            .font(.system(size: 12, weight: .bold))
                            .tracking(1)
                            .foregroundColor(.armyGold)

                        VStack(spacing: 8) {
                            MosCategoryButton(
                                title: "COMBAT ARMS",
                                subtitle: "Min 350 total, Gender-neutral scoring",
                                isSelected: selectedMosCategory == .combat,
                                action: { selectedMosCategory = .combat }
                            )
                            MosCategoryButton(
                                title: "COMBAT SUPPORT",
                                subtitle: "Min 300 total, Gender and age-normed",
                                isSelected: selectedMosCategory == .combatEnabling,
                                action: { selectedMosCategory = .combatEnabling }
                            )
                        }
                    }
                    .padding()
                    .background(Color.white.opacity(0.05))
                    .cornerRadius(12)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color.white.opacity(0.1), lineWidth: 1)
                    )

                    // Continue Button
                    Button(action: {
                        showCalculator = true
                    }) {
                        Text("ENTER SCORES")
                            .font(.system(size: 16, weight: .bold))
                            .tracking(1)
                            .foregroundColor(.armyBlack)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 16)
                            .background(Color.armyGold)
                            .cornerRadius(8)
                    }
                    .padding(.top, 8)

                    Spacer(minLength: 40)
                }
                .padding(.horizontal, 16)
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
                .font(.system(size: 14, weight: .bold))
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
                        .foregroundColor(isSelected ? .armyBlack : .white)
                    Text(subtitle)
                        .font(.system(size: 11))
                        .foregroundColor(isSelected ? .armyBlack.opacity(0.7) : .white.opacity(0.5))
                }
                Spacer()
                if isSelected {
                    Image(systemName: "checkmark.circle.fill")
                        .foregroundColor(.armyBlack)
                }
            }
            .padding()
            .background(isSelected ? Color.armyGold : Color.white.opacity(0.1))
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
