import Immutable from 'immutable'
import * as ActionTypes from '../constants/ActionTypes'

const initialState = Immutable.fromJS(BH.STORE.popConfig);

export default function template($template = initialState, action) {
    switch (action.type) {
        case ActionTypes.UPDATE_TEMPLATE:
            return action.$template;
        case ActionTypes.UPDATE_WIDGET:
            return $template.setIn(['widgets', action.wId], action.$widget);
        case ActionTypes.WIDGET_LINKAGE:
            return $template.setIn(['widgets', action.wId, 'clicked'], Immutable.fromJS(action.clicked));
        default:
            return $template
    }
}
