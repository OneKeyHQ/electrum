//
//  PanBottomAlertViewController.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/20.
//  Copyright © 2021 Onekey. All rights reserved.
//

import UIKit
import PanModal

class PanBottomAlertViewController: PanModalViewController {

    @IBOutlet weak var iconImageView: UIImageView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var contentLabel: UILabel!
    @IBOutlet weak var leftButton: UIButton!
    @IBOutlet weak var rightButton: UIButton!
    @IBOutlet weak var buttonSplitView: UIView!

    @IBOutlet weak var iconImageViewWidth: NSLayoutConstraint!
    @IBOutlet weak var iconImageViewTop: NSLayoutConstraint!
    @IBOutlet weak var leftButtonTrailing: NSLayoutConstraint!
    @IBOutlet weak var titleLabelTop: NSLayoutConstraint!
    @IBOutlet weak var contentLabelTop: NSLayoutConstraint!
    @IBOutlet weak var alertBottom: NSLayoutConstraint!

    private var icon: OKBottomAlertViewIcon?
    private var alertTitle: String = ""
    private var alertContent: String = ""
    private var leftAction: OKBottomAlertViewAction?
    private var rightAction: OKBottomAlertViewAction?

    @discardableResult
    @objc static func show(
        icon: OKBottomAlertViewIcon?,
        title: String,
        content: String,
        leftAction: OKBottomAlertViewAction,
        rightAction: OKBottomAlertViewAction?
    ) -> PanBottomAlertViewController {
        let alertView = PanBottomAlertViewController(
            icon: icon,
            title: title,
            content: content,
            leftAction: leftAction,
            rightAction: rightAction
        )
        OKTools.ok_TopViewController().presentPanModal(alertView)
        return alertView
    }

    init(
        icon: OKBottomAlertViewIcon?,
        title: String,
        content: String,
        leftAction: OKBottomAlertViewAction,
        rightAction: OKBottomAlertViewAction?
    ) {
        super.init(nibName: nil, bundle: nil)
        self.icon = icon
        self.alertTitle = title
        self.alertContent = content
        self.leftAction = leftAction
        self.rightAction = rightAction
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        configIcon(icon: icon)
        configTitleLabel(string: alertTitle)
        configContentLabel(string: alertContent)
        configButtons(leftAction: leftAction!, rightAction: rightAction)
    }

    private func configIcon(icon: OKBottomAlertViewIcon?) {
        if let icon = icon, !icon.string.isEmpty {
            switch icon.type {
            case .local:
                iconImageView.image = UIImage(named: icon.string)
            case .remote:
                iconImageView.setNetImage(
                    url: icon.string.addHttps,
                    placeholder: "logo_square"
                )
            case .loding:
                iconImageView.image = UIImage(named: icon.string)
                rotationImageView()
            }
        } else {
            iconImageViewWidth.constant = 0
            titleLabelTop.constant = 0
        }
    }

    private func configTitleLabel(string: String) {
        titleLabel.setText(string, lineSpacing: 1.2)
    }

    private func configContentLabel(string: String) {
        contentLabel.setText(string, lineSpacing: 1.2)
    }

    private func configButtons(
        leftAction: OKBottomAlertViewAction,
        rightAction: OKBottomAlertViewAction?) {
        leftButton.setTitleForAllStates(leftAction.title)
        leftButton.setTitleColorForAllStates(leftAction.color)
        leftButton.onTap { [weak self] in
            self?.dismiss(animated: true, completion: {
                leftAction.onTap?()
            })
        }
        if let rightAction = rightAction {
            rightButton.setTitleForAllStates(rightAction.title)
            rightButton.setTitleColorForAllStates(rightAction.color)
            rightButton.onTap { [weak self] in
                self?.dismiss(animated: true, completion: {
                    rightAction.onTap?()
                })
            }
        } else {
            rightButton.isHidden = true
            rightButton.isUserInteractionEnabled = false
            leftButtonTrailing.priority = .defaultHigh
            buttonSplitView.isHidden = true
        }
    }

    private func rotationImageView() {
        iconImageView.rotate(
            toAngle: CGFloat.pi * 2,
            ofType: .degrees,
            animated: true,
            duration: 0.01) { [weak self] _ in
            self?.rotationImageView()
        }
    }

    override var transitionDuration: Double {
        0.3
    }
}


@objc final class OKBottomAlertViewIcon: NSObject {

    enum OKBottomAlertViewIconType {
        case local
        case remote
        case loding
    }

    var string: String
    var type: OKBottomAlertViewIconType

    init(localImageName: String) {
        self.type = .local
        self.string = localImageName
    }

    init(remoteImageURLString: String) {
        self.type = .remote
        self.string = remoteImageURLString
    }

    init(lodingImage: String?) {
        self.type = .loding
        self.string = lodingImage ?? "quanquan"
    }

}

@objc final class OKBottomAlertViewAction: NSObject {

    enum OKBottomAlertViewIconType {
        case normal
        case highlight
    }

    var title: String
    var color: UIColor
    var onTap:(()->Void)?

    @objc init(normalTitle: String, onTap: (()->Void)?) {
        self.title = normalTitle
        self.color = .fg_B02()
        self.onTap = onTap
    }

    @objc init(highlightTitle: String, onTap: (()->Void)?) {
        self.title = highlightTitle
        self.color = .tintBrand()
        self.onTap = onTap
    }

    @objc init(warningTitle: String, onTap: (()->Void)?) {
        self.title = warningTitle
        self.color = .tintRed()
        self.onTap = onTap
    }

    @objc init(title: String, color: UIColor, onTap: @escaping ()->Void) {
        self.title = title
        self.color = color
        self.onTap = onTap
    }

}
