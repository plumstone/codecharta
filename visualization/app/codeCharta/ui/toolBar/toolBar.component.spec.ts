import { TestBed } from "@angular/core/testing"
import { render } from "@testing-library/angular"
import { LoadFileService } from "../../services/loadFile/loadFile.service"
import { hoveredNodeIdSelector } from "../../state/store/appStatus/hoveredNodeId/hoveredNodeId.selector"
import { ThreeCameraService } from "../codeMap/threeViewer/threeCamera.service"
import { ThreeRendererService } from "../codeMap/threeViewer/threeRenderer.service"
import { ThreeSceneService } from "../codeMap/threeViewer/threeSceneService"
import { ToolBarComponent } from "./toolBar.component"
import { appReducers, setStateMiddleware } from "../../state/store/state.manager"
import { StoreModule } from "@ngrx/store"
import { CopyToClipboardService } from "../../../codeCharta/ui/copyToClipboardButton/copyToClipboard.service"
import { FileSelectionModeService } from "../../../codeCharta/ui/filePanel/fileSelectionMode.service"

jest.mock("../../state/store/appStatus/hoveredNodeId/hoveredNodeId.selector", () => ({
    hoveredNodeIdSelector: jest.fn()
}))
const mockedHoveredNodeIdSelector = jest.mocked(hoveredNodeIdSelector)

describe("ToolBarComponent", () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [ToolBarComponent, StoreModule.forRoot(appReducers, { metaReducers: [setStateMiddleware] })],
            providers: [
                FileSelectionModeService,
                CopyToClipboardService,
                { provide: LoadFileService, useValue: {} },
                { provide: ThreeCameraService, useValue: {} },
                { provide: ThreeSceneService, useValue: {} },
                { provide: ThreeRendererService, useValue: {} }
            ]
        })
    })

    it("should show file panel and not hovered node path panel, when there is no node hovered", async () => {
        mockedHoveredNodeIdSelector.mockImplementation(() => null)
        const { container } = await render(ToolBarComponent)
        expect(container.querySelector("cc-file-panel")).not.toBe(null)
        expect(container.querySelector("cc-hovered-node-path-panel")).toBe(null)
    })

    it("should show hovered node path panel and not file panel, when there is a node hovered", async () => {
        mockedHoveredNodeIdSelector.mockImplementation(() => 0)
        const { container } = await render(ToolBarComponent)
        expect(container.querySelector("cc-hovered-node-path-panel")).not.toBe(null)
        expect(container.querySelector("cc-file-panel")).toBe(null)
    })
})
