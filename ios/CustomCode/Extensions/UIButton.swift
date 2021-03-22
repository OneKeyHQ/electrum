// UIButtonExtensions.swift - Copyright 2020 SwifterSwift

import UIKit

// MARK: - Methods

public extension UIButton {
    private var states: [UIControl.State] {
        return [.normal, .selected, .highlighted, .disabled]
    }

    /// SwifterSwift: Set image for all states.
    ///
    /// - Parameter image: UIImage.
    func setImageForAllStates(_ image: UIImage) {
        states.forEach { setImage(image, for: $0) }
    }

    /// SwifterSwift: Set title color for all states.
    ///
    /// - Parameter color: UIColor.
    func setTitleColorForAllStates(_ color: UIColor) {
        states.forEach { setTitleColor(color, for: $0) }
    }

    /// SwifterSwift: Set title for all states.
    ///
    /// - Parameter title: title string.
    func setTitleForAllStates(_ title: String) {
        states.forEach { setTitle(title, for: $0) }
    }

    /// SwifterSwift: Center align title text and image.
    /// - Parameters:
    ///   - imageAboveText: set true to make image above title text, default is false, image on left of text.
    ///   - spacing: spacing between title text and image.
    func centerTextAndImage(imageAboveText: Bool = false, spacing: CGFloat) {
        if imageAboveText {
            // https://stackoverflow.com/questions/2451223/#7199529
            guard
                let imageSize = imageView?.image?.size,
                let text = titleLabel?.text,
                let font = titleLabel?.font else { return }

            let titleSize = text.size(withAttributes: [.font: font])

            let titleOffset = -(imageSize.height + spacing)
            titleEdgeInsets = UIEdgeInsets(top: 0.0, left: -imageSize.width, bottom: titleOffset, right: 0.0)

            let imageOffset = -(titleSize.height + spacing)
            imageEdgeInsets = UIEdgeInsets(top: imageOffset, left: 0.0, bottom: 0.0, right: -titleSize.width)

            let edgeOffset = abs(titleSize.height - imageSize.height) / 2.0
            contentEdgeInsets = UIEdgeInsets(top: edgeOffset, left: 0.0, bottom: edgeOffset, right: 0.0)
        } else {
            let insetAmount = spacing / 2
            imageEdgeInsets = UIEdgeInsets(top: 0, left: -insetAmount, bottom: 0, right: insetAmount)
            titleEdgeInsets = UIEdgeInsets(top: 0, left: insetAmount, bottom: 0, right: -insetAmount)
            contentEdgeInsets = UIEdgeInsets(top: 0, left: insetAmount, bottom: 0, right: insetAmount)
        }
    }
}

extension UIButton {

    private struct RuntimeKey {
        static let clickEdgeInsets = UnsafeRawPointer.init(bitPattern: "clickEdgeInsets".hashValue)
    }

    public var expansionInsets: UIEdgeInsets? {
        set {
            guard let key = UIButton.RuntimeKey.clickEdgeInsets else { return }
            objc_setAssociatedObject(self, key, newValue, objc_AssociationPolicy.OBJC_ASSOCIATION_COPY)
        }
        get {
            guard let key = UIButton.RuntimeKey.clickEdgeInsets else { return nil }
            return objc_getAssociatedObject(self, key) as? UIEdgeInsets ?? UIEdgeInsets.zero
        }
    }

    open override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
        super.point(inside: point, with: event)
        var bounds = self.bounds
        if (expansionInsets != nil) {
            let x: CGFloat = -(expansionInsets?.left ?? 0)
            let y: CGFloat = -(expansionInsets?.top ?? 0)
            let width: CGFloat = bounds.width + (expansionInsets?.left ?? 0) + (expansionInsets?.right ?? 0)
            let height: CGFloat = bounds.height + (expansionInsets?.top ?? 0) + (expansionInsets?.bottom ?? 0)
            bounds = CGRect(x: x, y: y, width: width, height: height)
        }
        return bounds.contains(point)
    }

}

extension UIButton {

    @discardableResult
    public func onTap(handler: @escaping () -> Void) -> Self {
        addAction(for: .touchUpInside) { (_) in
            handler()
        }
        return self
    }
}
