//
//  PanModalViewController.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/20.
//  Copyright © 2021 Onekey. All rights reserved.
//

import UIKit
import PanModal

class PanModalViewController: ViewController, PanModalPresentable {

    override func viewDidLoad() {
        super.viewDidLoad()

    }

    // MARK: - Pan Modal Presentable

    var panScrollable: UIScrollView? {
        nil
    }
    
    var longFormHeight: PanModalHeight {
        .intrinsicHeight
    }

    var shortFormHeight: PanModalHeight {
        .intrinsicHeight
    }
    
    var panModalBackgroundColor: UIColor {
        .fg_B04()
    }
    
    var showDragIndicator: Bool {
        false
    }
    
    var cornerRadius: CGFloat {
        20
    }
    
    var isHapticFeedbackEnabled: Bool {
        false
    }

    var shouldRoundTopCorners: Bool {
        true
    }

    var allowsDragToDismiss: Bool {
        false
    }
    
    var allowsTapToDismiss: Bool {
        false
    }

    var isUserInteractionEnabled: Bool {
         true
    }
    
    func shouldRespond(to panModalGestureRecognizer: UIPanGestureRecognizer) -> Bool {
        false
    }
    
    var transitionDuration: Double {
        0.5
    }
}
