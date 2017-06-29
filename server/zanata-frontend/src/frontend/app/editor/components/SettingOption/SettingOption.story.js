import React from 'react'
import PropTypes from 'prop-types'
import { storiesOf, action } from '@kadira/storybook'
import RealSettingOption from '.'

class SettingOption extends React.Component {
  static propTypes = {
    setting: PropTypes.shape.isRequired,
    updateSetting: PropTypes.func.isRequired
  }
  constructor (props) {
    super(props)
    this.state = props.state
  }
  updateSetting = (setting, checked) => {
    // record the check state in the wrapper
    this.setState({ [setting]: checked })
    // call the real one that was passed in
    this.props.updateSetting(setting, checked)
  }
  render () {
    return (
        <RealSettingOption
            updateSetting={this.updateSetting}
            states={this.state} />
    )
  }
}

const updateAction = action('updateSetting')

storiesOf('SettingOption', module)
  .add('default', () => (
      <SettingOption
        setting={{
          id: 'Ambulance',
          label: 'Krankenwagen',
          active: {true}
     }}
      updateSetting={action(updateAction)} />
  ))
