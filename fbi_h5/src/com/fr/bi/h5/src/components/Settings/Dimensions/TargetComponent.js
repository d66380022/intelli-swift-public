import mixin from 'react-mixin'
import {findDOMNode} from 'react-dom'
import Immutable from 'immutable'

import {
    ReactComponentWithPureRenderMixin, ReactComponentWithImmutableRenderMixin,
    cn, sc, math, isNil, emptyFunction, shallowEqual, immutableShallowEqual, isEqual, isEmpty, each,
    translateDOMPositionXY, requestAnimationFrame
} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Portal,
    PixelRatio,
    ListView,
    View,
    Fetch,
    Promise,
    TouchableHighlight
} from 'lib'

import {Colors, Sizes, TemplateFactory, WidgetFactory, DimensionFactory} from 'data'

import {Layout, CenterLayout, HorizontalCenterLayout, VerticalCenterLayout} from 'layout';

import {Button, IconButton, TextButton, Table, ActionSheet} from 'base'

import {MultiSelectorWidget} from 'widgets'

import TargetComponentHelper from './TargetComponentHelper'
import TargetSortComponent from './Sort/TargetSortComponent'


class TargetComponent extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {
        wId: '',
        $widget: null,
        dId: '',
        value: {}
    };

    state = {};

    _getNextState(props, state = {}) {

    }

    componentWillMount() {

    }

    componentDidMount() {

    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        this._helper = new TargetComponentHelper(props, this.context);
        return <Button onPress={()=> {
            this.props.onValueChange(this._helper.switchSelect());
        }}>
            <Layout main='justify' box='last' style={styles.wrapper}>
                <Layout cross='center' box='first'>
                    <IconButton style={styles.icon} invalid={true} selected={this._helper.isUsed()}
                                className={'single-select-font'}/>
                    <Text numberOfLines={1} style={sc([styles.disabledText, !this._helper.isUsed()])}
                          effect={false}>{props.value.text}</Text>
                </Layout>
                <IconButton style={styles.sortIcon} onPress={()=> {
                    Portal.showModal('DimensionSort', <ActionSheet
                        title={`"${this._helper.getSortTargetName()}"排序`}
                        onClose={(tag)=> {
                            if (tag === '取消') {

                            } else if (tag === '确定') {
                                this.props.onValueChange(this._$widget);
                            }
                            Portal.closeModal('DimensionSort')
                        }}
                    >
                        <TargetSortComponent
                            wId={props.wId} $widget={props.$widget} dId={props.value.dId}
                            onValueChange={($widget)=> {
                                this._$widget = $widget;
                            }}
                        />
                    </ActionSheet>)
                }}
                            className={this._helper.getSortTargetTypeFont()}/>
            </Layout>
        </Button>
    }

    componentWillReceiveProps(nextProps) {

    }

    componentWillUpdate(nextProps, nextState) {

    }

    componentDidUpdate(prevProps, prevState) {

    }

    componentWillUnmount() {

    }

}
mixin.onClass(TargetComponent, ReactComponentWithImmutableRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        paddingLeft: 20,
        paddingRight: 20,
        height: Sizes.ITEM_HEIGHT,
        borderBottomWidth: 1 / PixelRatio.get(),
        borderBottomColor: Colors.BORDER
    },
    icon: {
        width: 40,
    },

    sortIcon: {
        width: 20
    },

    sortTargetName: {
        paddingLeft: 10,
        paddingRight: 10,
        textAlign: 'right'
    },

    disabledText: {
        color: Colors.DISABLED
    }
});
export default TargetComponent
