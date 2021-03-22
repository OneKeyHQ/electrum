//
//  PanModalNavViewController.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/20.
//  Copyright © 2021 Onekey. All rights reserved.
//

import UIKit
import PanModal

class PanModalNavViewController: BaseNavigationController, PanModalPresentable {
    
    init(viewControllers: [UIViewController]) {
        super.init(nibName: nil, bundle: nil)
        self.viewControllers = viewControllers
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func popViewController(animated: Bool) -> UIViewController? {
        let vc = super.popViewController(animated: animated)
        panModalSetNeedsLayoutUpdate()
        return vc
    }

    override func pushViewController(_ viewController: UIViewController, animated: Bool) {
        super.pushViewController(viewController, animated: animated)
        panModalSetNeedsLayoutUpdate()
    }

    // MARK: - Pan Modal Presentable
    
    private var panModalPresentable: PanModalPresentable? {
        viewControllers.last as? PanModalPresentable
    }
    
    var panScrollable: UIScrollView? {
        panModalPresentable?.panScrollable
    }

    var longFormHeight: PanModalHeight {
        panModalPresentable?.longFormHeight ?? .intrinsicHeight
    }

    var shortFormHeight: PanModalHeight {
        panModalPresentable?.shortFormHeight ?? longFormHeight
    }

    var panModalBackgroundColor: UIColor {
        panModalPresentable?.panModalBackgroundColor ?? .fg_B04()
    }
    
    var showDragIndicator: Bool {
        panModalPresentable?.showDragIndicator ?? false
    }
    
    var cornerRadius: CGFloat {
        panModalPresentable?.cornerRadius ?? 20
    }
    
    var isHapticFeedbackEnabled: Bool {
        panModalPresentable?.isHapticFeedbackEnabled ?? false
    }

    var shouldRoundTopCorners: Bool {
        panModalPresentable?.shouldRoundTopCorners ?? true
    }

    var allowsDragToDismiss: Bool {
        panModalPresentable?.allowsDragToDismiss ?? false
    }
    
    var allowsTapToDismiss: Bool {
        panModalPresentable?.allowsTapToDismiss ?? false
    }
    
    var isUserInteractionEnabled: Bool {
        panModalPresentable?.isUserInteractionEnabled ?? true
    }
    
    func shouldRespond(to panModalGestureRecognizer: UIPanGestureRecognizer) -> Bool {
        panModalPresentable?.shouldRespond(to: panModalGestureRecognizer) ?? false
    }
    
    var transitionDuration: Double {
        panModalPresentable?.transitionDuration ?? 0.5
    }
}
