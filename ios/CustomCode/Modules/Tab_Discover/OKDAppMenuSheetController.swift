//
//  OKDAppMenuSheetController.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/23.
//  Copyright © 2021 Onekey. All rights reserved.
//

import UIKit
import Reusable
import PanModal

final class OKDAppMenuSheetController: PanModalViewController {

    private var model: OKWebJSModel?

    static func show(model: OKWebJSModel, tapAction: @escaping ((OKDAppMenuType) -> Void)) {
        let page = OKDAppMenuSheetController.instantiate()
        page.model = model
        page.tapAction = tapAction
        OKTools.ok_TopViewController().presentPanModal(page)
    }

    @IBOutlet weak var dappIcon: UIImageView!
    @IBOutlet weak var dappTitle: UILabel!
    @IBOutlet weak var dappDes: UILabel!
    @IBOutlet weak var cancelButton: UIButton!
    @IBOutlet weak var collectionView: UICollectionView!

    var tapAction: ((OKDAppMenuType) -> Void)?

    private lazy var dataSource: [OKDAppMenuType] = {
        return [
            .switchAccount,
//            .collect, .onekeyKeys, .floatingWindow,
            .refresh, .share, .copyURL, .openInSafari
        ]
    }()

    override func viewDidLoad() {
        super.viewDidLoad()
        cancelButton.setTitle("cancel".localized, for: .normal)
        cancelButton.onTap { [weak self] in
            guard let self = self else { return }
            self.dismiss(animated: true, completion: nil)
        }

        if let model = model?.jsParams() {
            dappTitle.text = model.name ?? "DApp"
            dappDes.text = model.description ?? "--"
            dappIcon.setNetImage(url: (model.img ?? "").addHttps)
        }

        let layout = UICollectionViewFlowLayout()
        layout.scrollDirection = .vertical
        layout.itemSize = .init(width: 70, height: 80)
        layout.minimumLineSpacing = 20
        layout.sectionInset = .init(top: 24, left: 24, bottom: 24, right: 24)
        collectionView.collectionViewLayout = layout
        collectionView.delegate = self
        collectionView.dataSource = self
        collectionView.register(cellType: OKDAppMenuSheetCollectionCell.self)
    }

    override var transitionDuration: Double {
        0.3
    }

    override var allowsDragToDismiss: Bool {
        true
    }

    override var allowsTapToDismiss: Bool {
        true
    }

    override func shouldRespond(to panModalGestureRecognizer: UIPanGestureRecognizer) -> Bool {
        true
    }
}

// MARK: - Pan Modal Presentable

extension OKDAppMenuSheetController: UICollectionViewDelegate, UICollectionViewDataSource {

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return dataSource.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(for: indexPath, cellType: OKDAppMenuSheetCollectionCell.self)
        let m = dataSource[indexPath.row]
        cell.setModel(type: m, coinType: model?.jsParams()?.chain ?? "token_eth")
        return cell
    }

    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let model = dataSource[indexPath.row]
        dismiss(animated: true) { [weak self] in
            guard let self = self else { return }
            self.tapAction?(model)
        }
    }

}

extension OKDAppMenuSheetController: StoryboardSceneBased {
    static let sceneStoryboard = UIStoryboard(name: "Tab_Discover", bundle: nil)
    static var sceneIdentifier = "OKDAppMenuSheetController"
}
